import { NextResponse } from 'next/server';

const CLAUDE_MODEL = 'claude-sonnet-4-6';

const AI_SYSTEM = `You are the backend of a Service Registry. Given a registry node (JSON), return a JSON object with these exact fields:
- summary: 1 plain-English sentence describing what this node does in the architecture
- insights: array of exactly 2 short strings (operational notes or risk observations)
- suggestedTags: array of 4 relevant lowercase string tags
Return valid JSON only. No markdown, no prose, no backticks.`;

export async function POST(request) {
  try {
    const { node } = await request.json();

    const apiKey = process.env.ANTHROPIC_API_KEY;

    // If no API key configured, return a graceful stub so the UI still works
    if (!apiKey) {
      return NextResponse.json({
        summary: `${node.name} is a ${node.kind?.toLowerCase().replace('_', ' ')} in the ${node.domain} domain.`,
        insights: [
          'Set ANTHROPIC_API_KEY in .env.local to enable real AI insights.',
          `Owned by ${node.team || 'unknown team'} · tech: ${node.tech || 'unspecified'}.`,
        ],
        suggestedTags: [
          node.domain?.toLowerCase() || 'domain',
          node.kind?.toLowerCase().replace('_', '-') || 'service',
          node.tech?.split('/')[0]?.trim().toLowerCase() || 'backend',
          'registry',
        ],
      });
    }

    const res = await fetch('https://api.anthropic.com/v1/messages', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'x-api-key': apiKey,
        'anthropic-version': '2023-06-01',
      },
      body: JSON.stringify({
        model: CLAUDE_MODEL,
        max_tokens: 600,
        system: AI_SYSTEM,
        messages: [{ role: 'user', content: JSON.stringify(node) }],
      }),
    });

    const data = await res.json();
    const text =
      data.content
        ?.filter((b) => b.type === 'text')
        .map((b) => b.text)
        .join('') ?? '';

    const parsed = JSON.parse(text.replace(/```json|```/g, '').trim());
    return NextResponse.json(parsed);
  } catch (err) {
    return NextResponse.json(
      { error: err.message },
      { status: 500 }
    );
  }
}
