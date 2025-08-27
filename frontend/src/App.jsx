import React, { useEffect, useState } from 'react'

function App() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [quiz, setQuiz] = useState(null)
  const [proposedBid, setProposedBid] = useState('')
  const [result, setResult] = useState(null)
  const [selectedConvention, setSelectedConvention] = useState('')

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
      setSelectedConvention(data?.convention || '')
    } catch (e) {
      setError(e.message || 'Failed to load quiz')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
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
    <div style={{ maxWidth: 900, margin: '2rem auto', fontFamily: 'system-ui, Arial' }}>
      <h1>Bridge Bid Quiz</h1>

      <div style={{ margin: '0.5rem 0 1rem' }}>
        <button onClick={fetchQuiz} disabled={loading} style={{ padding: '6px 12px' }}>
          {loading ? 'Loading…' : 'New Question'}
        </button>
      </div>

      {loading && <p>Loading quiz…</p>}
      {error && <p style={{ color: 'crimson' }}>{error}</p>}

      {quiz && (
        <div style={{ border: '1px solid #ddd', borderRadius: 8, padding: 16 }}>
          <h2>Hand</h2>
          <pre style={{ background: '#f8f8f8', padding: 12 }}>
{quiz.hand}
          </pre>
          <p><strong>Position:</strong> {quiz.position}</p>
          <div style={{ margin: '8px 0' }}>
            <label>
              <strong>Convention: </strong>
              <select
                value={selectedConvention}
                onChange={(e) => setSelectedConvention(e.target.value)}
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

          <form onSubmit={submitBid} style={{ marginTop: 16 }}>
            <label>
              Your proposed bid: {' '}
              <input
                value={proposedBid}
                onChange={(e) => setProposedBid(e.target.value)}
                placeholder="e.g. 1C, 1H, PASS"
                style={{ padding: '6px 8px', fontSize: 16 }}
                required
              />
            </label>
            <button type="submit" style={{ marginLeft: 12, padding: '6px 12px' }}>Check</button>
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
    </div>
  )
}

export default App
