import React, { useEffect, useState } from 'react'

function App() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [quiz, setQuiz] = useState(null)
  const [proposedBid, setProposedBid] = useState('')
  const [result, setResult] = useState(null)
  const [selectedConvention, setSelectedConvention] = useState('')
  const [showDealsModal, setShowDealsModal] = useState(false)
  const [deals, setDeals] = useState([])
  const [dealsLoading, setDealsLoading] = useState(false)
  const [dealsError, setDealsError] = useState('')
  const [dealsLimit, setDealsLimit] = useState(50)
  const CONV_KEY = 'bbq.convention'

  const BID_OPTIONS = React.useMemo(() => {
    const opts = ['PASS']
    const suits = ['C', 'D', 'H', 'S', 'NT']
    for (let lvl = 1; lvl <= 7; lvl++) {
      for (const s of suits) opts.push(`${lvl}${s}`)
    }
    return opts
  }, [])

  const fetchRecentDeals = async (limit = dealsLimit) => {
    try {
      setDealsLoading(true)
      setDealsError('')
      const res = await fetch(`/api/deals/recent?limit=${encodeURIComponent(limit)}`)
      if (!res.ok) throw new Error(`Failed to load deals (${res.status})`)
      const data = await res.json()
      setDeals(Array.isArray(data) ? data : [])
    } catch (e) {
      setDealsError(e.message || 'Failed to load recent deals')
    } finally {
      setDealsLoading(false)
    }
  }

  const formatBidLabel = (b) => {
    if (b === 'PASS') return 'PASS'
    const m = b.match(/^([1-7])(C|D|H|S|NT)$/)
    if (!m) return b
    const [, lvl, s] = m
    const sym = s === 'C' ? '♣' : s === 'D' ? '♦' : s === 'H' ? '♥' : s === 'S' ? '♠' : 'NT'
    return s === 'NT' ? `${lvl}NT` : `${lvl} ${sym}`
  }

  const bidColor = (b) => {
    const m = b.match(/^(PASS|[1-7](C|D|H|S|NT))$/)
    if (!m) return undefined
    const suit = b.endsWith('NT') ? 'NT' : b.slice(-1)
    if (suit === 'H' || suit === 'D') return '#c00'
    return undefined
  }

  const HandDisplay = ({ hand }) => {
    const parts = (hand || '').trim().split('.')
    const [sp, he, di, cl] = [parts[0] || '', parts[1] || '', parts[2] || '', parts[3] || '']
    const rows = [
      { sym: '♠', text: sp, color: '#111' },
      { sym: '♥', text: he, color: '#c00' },
      { sym: '♦', text: di, color: '#c00' },
      { sym: '♣', text: cl, color: '#111' },
    ]
    return (
      <div style={{ background: '#f8f8f8', padding: 12, borderRadius: 6, fontFamily: 'ui-monospace, SFMono-Regular, Menlo, Consolas, monospace' }}>
        {rows.map((r, i) => (
          <div key={i} style={{ lineHeight: 1.6 }}>
            <span style={{ color: r.color, fontWeight: 700, display: 'inline-block', width: 20 }}>{r.sym}</span>
            <span>{r.text && r.text !== '-' ? r.text : '—'}</span>
          </div>
        ))}
      </div>
    )
  }

  // Compact table layout for a full bridge deal (N/W/E/S)
  const DealDiagram = ({ deal }) => {
    const north = deal?.northHand
    const east = deal?.eastHand
    const south = deal?.southHand
    const west = deal?.westHand
    const dealer = deal?.dealer
    const convention = deal?.convention
    const createdAt = deal?.createdAt
    // Parse auction from JSON array or fallback comma-separated string
    const rawAuction = deal?.auctionJson
    let auction = []
    try {
      if (rawAuction) {
        if (typeof rawAuction === 'string' && rawAuction.trim().startsWith('[')) {
          auction = JSON.parse(rawAuction)
        } else if (typeof rawAuction === 'string') {
          auction = rawAuction.split(',').map(s => s.trim()).filter(Boolean)
        } else if (Array.isArray(rawAuction)) {
          auction = rawAuction
        }
      }
    } catch (_) {
      // ignore parse errors, leave auction empty
    }
    return (
      <div style={{
        border: '1px solid #ddd',
        borderRadius: 8,
        padding: 12,
        background: '#fff',
      }}>
        <div style={{ fontSize: 12, marginBottom: 8, color: '#555', display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
          <span><strong>Dealer:</strong> {dealer || '—'}</span>
          {convention && (
            <span style={{
              padding: '2px 6px',
              border: '1px solid #cfe6cf',
              background: '#eef6ee',
              borderRadius: 999,
              color: '#0b5d36',
              fontWeight: 600
            }} title="Bidding convention">
              {convention}
            </span>
          )}
          {createdAt && (
            <span title={createdAt} style={{ color: '#666' }}>
              {formatDateTime(createdAt)}
            </span>
          )}
        </div>
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr 1fr',
          gridTemplateRows: 'auto auto auto',
          gap: 8,
          alignItems: 'center',
          justifyItems: 'center'
        }}>
          <div style={{ gridColumn: '2 / 3' }}>
            <div style={{ textAlign: 'center', fontWeight: 600, marginBottom: 4 }}>North</div>
            <HandDisplay hand={north} />
          </div>
          <div style={{ gridColumn: '1 / 2', gridRow: '2 / 3' }}>
            <div style={{ textAlign: 'center', fontWeight: 600, marginBottom: 4 }}>West</div>
            <HandDisplay hand={west} />
          </div>
          <div style={{ gridColumn: '3 / 4', gridRow: '2 / 3' }}>
            <div style={{ textAlign: 'center', fontWeight: 600, marginBottom: 4 }}>East</div>
            <HandDisplay hand={east} />
          </div>
          <div style={{ gridColumn: '2 / 3', gridRow: '3 / 4' }}>
            <div style={{ textAlign: 'center', fontWeight: 600, marginBottom: 4 }}>South</div>
            <HandDisplay hand={south} />
          </div>
        </div>
        <div style={{ marginTop: 10, fontSize: 12 }}>
          <strong>Auction:</strong>{' '}
          {auction.length ? (
            <span style={{ display: 'inline-flex', flexWrap: 'wrap', gap: 6 }}>
              {auction.map((b, idx) => (
                <span
                  key={idx}
                  style={{
                    display: 'inline-block',
                    padding: '2px 6px',
                    borderRadius: 999,
                    border: '1px solid #ddd',
                    background: '#f9f9f9',
                    color: bidColor(b) || '#111',
                    fontWeight: 600,
                    lineHeight: 1.2
                  }}
                  title={b}
                >
                  {formatBidLabel(b)}
                </span>
              ))}
            </span>
          ) : (
            <span>—</span>
          )}
        </div>
      </div>
    )
  }

  const computeHcp = (hand) => {
    if (!hand) return 0
    const points = { A: 4, K: 3, Q: 2, J: 1 }
    let total = 0
    for (const ch of hand.replaceAll('.', '')) {
      if (points[ch]) total += points[ch]
    }
    return total
  }

  const formatDateTime = (iso) => {
    try {
      const d = new Date(iso)
      if (isNaN(d.getTime())) return iso
      return d.toLocaleString()
    } catch (_) {
      return iso || ''
    }
  }

  const fetchQuiz = async () => {
    try {
      setLoading(true)
      setError('')
      setResult(null)
      setProposedBid('')
      const res = await fetch('/api/bids/quiz')
      if (!res.ok) throw new Error(`Failed to load quiz (${res.status})`)
      const data = await res.json()
      setQuiz(data)
      // If user has no saved selection yet, fall back to quiz default
      const saved = localStorage.getItem(CONV_KEY)
      if (!saved) {
        setSelectedConvention(data?.convention || 'natural')
      }
    } catch (e) {
      setError(e.message || 'Failed to load quiz')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    // Initialize convention from localStorage if present
    try {
      const saved = localStorage.getItem(CONV_KEY)
      if (saved) setSelectedConvention(saved)
    } catch (_) { /* ignore */ }
    fetchQuiz()
  }, [])

  const submitBid = async (e) => {
    e.preventDefault()
    if (!quiz) return
    try {
      setError('')
      setResult(null)
      const payload = {
        proposedBid,
        hand: quiz.hand,
        position: quiz.position,
        convention: selectedConvention || quiz.convention,
        auction: quiz.auction,
      }
      const res = await fetch('/api/bids/check', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      if (!res.ok) throw new Error(`Check failed (${res.status})`)
      const data = await res.json()
      setResult(data)
    } catch (e) {
      setError(e.message || 'Failed to check bid')
    }
  }

  return (
    <div style={{
      maxWidth: 900,
      margin: '2rem auto',
      fontFamily: 'system-ui, Arial',
      background: 'var(--panel-bg)',
      borderRadius: 12,
      padding: 16,
      boxShadow: '0 8px 24px rgba(0,0,0,0.18)'
    }}>
      <h1>Bridge Bid Quiz</h1>

      <div style={{ margin: '0.5rem 0 1rem' }}>
        <button onClick={fetchQuiz} disabled={loading} style={{ padding: '6px 12px' }}>
          {loading ? 'Loading…' : 'New Question'}
        </button>
        <button
          onClick={() => {
            setShowDealsModal(true)
            if (!deals.length) fetchRecentDeals()
          }}
          style={{ padding: '6px 12px', marginLeft: 8 }}
        >
          Recent Deals
        </button>
      </div>

      {loading && <p>Loading quiz…</p>}
      {error && <p style={{ color: 'crimson' }}>{error}</p>}

      {quiz && (
        <div style={{ border: '1px solid #ddd', borderRadius: 8, padding: 16 }}>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            Hand
            <span style={{
              fontSize: 14,
              background: '#eef6ee',
              border: '1px solid #cfe6cf',
              padding: '2px 8px',
              borderRadius: 999,
              color: '#0b5d36'
            }}>
              HCP: {computeHcp(quiz.hand)}
            </span>
          </h2>
          <HandDisplay hand={quiz.hand} />
          <p><strong>Position:</strong> {quiz.position}</p>
          <div style={{ margin: '8px 0' }}>
            <label>
              <strong>Convention: </strong>
              <select
                value={selectedConvention}
                onChange={(e) => {
                  const val = e.target.value
                  setSelectedConvention(val)
                  try { localStorage.setItem(CONV_KEY, val) } catch (_) { /* ignore */ }
                }}
                disabled={loading}
                style={{ marginLeft: 6, padding: '6px 8px' }}
              >
                <option value="natural">Natural (SAYC)</option>
                <option value="2/1">2/1 Game Force</option>
                <option value="precision">Precision (Strong Club)</option>
                <option value="polish club">Polish Club</option>
                <option value="acol">Acol</option>
              </select>
            </label>
          </div>
          <p><strong>Previous bids:</strong> {quiz.auction && quiz.auction.length ? quiz.auction.join(', ') : '—'}</p>

          <form onSubmit={submitBid} style={{ marginTop: 16, display: 'flex', gap: 12, alignItems: 'center' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              Your proposed bid:
              <select
                value={proposedBid}
                onChange={(e) => setProposedBid(e.target.value)}
                style={{ padding: '6px 8px', fontSize: 16 }}
                required
              >
                <option value="" disabled>Select bid…</option>
                {BID_OPTIONS.map(b => (
                  <option key={b} value={b} style={{ color: bidColor(b) }}>{formatBidLabel(b)}</option>
                ))}
              </select>
            </label>
            <button type="submit" style={{ padding: '6px 12px' }}>Check</button>
          </form>

          {result && (
            <div style={{ marginTop: 16, background: '#f5faff', border: '1px solid #cce0ff', padding: 12, borderRadius: 6 }}>
              <h3>Check Result</h3>
              <p><strong>Suggested optimal bid:</strong> {result.suggestedBid}</p>
              <p><strong>Explanation:</strong> {result.explanation}</p>
            </div>
          )}
        </div>
      )}

      {showDealsModal && (
        <div
          role="dialog"
          aria-modal="true"
          style={{
            position: 'fixed', inset: 0,
            background: 'rgba(0,0,0,0.4)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            zIndex: 9999
          }}
          onClick={(e) => {
            if (e.target === e.currentTarget) setShowDealsModal(false)
          }}
        >
          <div style={{
            width: 'min(1000px, 96vw)',
            maxHeight: '90vh',
            background: 'var(--panel-bg)',
            borderRadius: 12,
            boxShadow: '0 12px 32px rgba(0,0,0,0.35)',
            overflow: 'hidden',
            display: 'flex',
            flexDirection: 'column'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px', borderBottom: '1px solid #ddd' }}>
              <h2 style={{ margin: 0 }}>Recent Deals</h2>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <label style={{ fontSize: 14 }}>
                  Show:
                  <select
                    value={dealsLimit}
                    onChange={(e) => {
                      const v = Number(e.target.value) || 50
                      setDealsLimit(v)
                      fetchRecentDeals(v)
                    }}
                    style={{ marginLeft: 6, padding: '4px 6px' }}
                  >
                    <option value={20}>20</option>
                    <option value={50}>50</option>
                    <option value={100}>100</option>
                  </select>
                </label>
                <button onClick={() => setShowDealsModal(false)} style={{ padding: '6px 10px' }}>Close</button>
              </div>
            </div>
            <div style={{ padding: 16, overflow: 'auto' }}>
              {dealsLoading && <p>Loading deals…</p>}
              {dealsError && <p style={{ color: 'crimson' }}>{dealsError}</p>}
              {!dealsLoading && !dealsError && (!deals || deals.length === 0) && (
                <p>No deals found.</p>
              )}
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 12 }}>
                {deals && deals.map((d) => (
                  <DealDiagram key={d.id} deal={d} />
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
